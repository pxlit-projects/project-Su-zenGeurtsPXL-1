import {Component, inject, OnInit} from '@angular/core';
import {Router, RouterLink, RouterLinkActive} from "@angular/router";
import {AsyncPipe, NgOptimizedImage} from "@angular/common";
import {AuthenticationService} from "../../shared/services/authentication/authentication.service";
import {PostService} from "../../shared/services/post/post.service";
import {Notification} from "../../shared/models/posts/notification.model";
import {interval, tap} from "rxjs";

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, NgOptimizedImage, AsyncPipe],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent implements OnInit {
  protected readonly localStorage = localStorage;
  router: Router = inject(Router);
  authenticationService: AuthenticationService = inject(AuthenticationService);
  postService: PostService = inject(PostService);
  userMenuIsHidden: boolean = true;
  notificationsMenuIsHidden: boolean = true;
  notifications$: Notification[] | undefined;
  hasNoNotifications: boolean = true;

  ngOnInit(): void {
    this.fetchNotifications();
    interval(1000).subscribe(() => this.fetchNotifications());
  }

  fetchNotifications() {
    if (localStorage.getItem('userRole') !== 'editor') return;
    this.postService.getMyNotifications()
      .pipe(tap())
      .subscribe(notifications => {
        this.notifications$ = notifications.filter(notification => !notification.isRead);
        this.hasNoNotifications = this.notifications$.length == 0;
      });
  }

  userMenu() {
    this.userMenuIsHidden = !this.userMenuIsHidden;
    this.notificationsMenuIsHidden = true;
  }

  notificationsMenu() {
    this.notificationsMenuIsHidden = !this.notificationsMenuIsHidden;
    this.userMenuIsHidden = true;
  }

  login() {
    this.router.navigate(['/login']);
  }

  logout() {
    this.userMenuIsHidden = true;
    this.notificationsMenuIsHidden = true;
    this.localStorage.removeItem('userId');
    this.localStorage.removeItem('userFullName');
    this.localStorage.removeItem('userRole');
    this.router.navigate(['/post']);
  }

  openPost(postId: number, notificationId: number) {
    this.postService.markNotificationAsRead(notificationId).subscribe(() => {
      this.router.navigate(['/myPost/' + postId]);
      this.userMenuIsHidden = true;
      this.notificationsMenuIsHidden = true;
    });
  }
}
