import {Component, inject, OnDestroy, OnInit} from '@angular/core';
import {Router, RouterLink, RouterLinkActive} from "@angular/router";
import {AsyncPipe, NgOptimizedImage} from "@angular/common";
import {AuthenticationService} from "../../shared/services/authentication/authentication.service";
import {PostService} from "../../shared/services/post/post.service";
import {Notification} from "../../shared/models/posts/notification.model";
import {interval, Observable, Subscription} from "rxjs";
import {PostItemComponent} from "../posts/post-item/post-item.component";

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, NgOptimizedImage, AsyncPipe, PostItemComponent],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent implements OnInit, OnDestroy{
  protected readonly localStorage = localStorage;
  router: Router = inject(Router);
  authenticationService: AuthenticationService = inject(AuthenticationService);
  postService: PostService = inject(PostService);
  userMenuIsHidden: boolean = true;
  notificationsMenuIsHidden: boolean = true;
  notifications$!: Observable<Notification[]>;
  hasNoNotifications: boolean = true;

  private fetchSubscription: Subscription | undefined;

  ngOnInit(): void {
    this.fetchNotifications();
    this.fetchSubscription = interval(5000).subscribe(() => this.fetchNotifications());
  }

  ngOnDestroy(): void {
    this.fetchSubscription?.unsubscribe();
  }

  fetchNotifications() {
    if (localStorage.getItem('userRole') === 'editor') {
      let fetchedNotifications = this.postService.getMyNotifications();
      if (this.notifications$ === undefined) {
        this.handleFetch(fetchedNotifications);
      } else {
        fetchedNotifications.subscribe(fetchedNotificationsData => {
          this.notifications$.subscribe(notifications => {
            if (notifications != fetchedNotificationsData) {
              this.handleFetch(fetchedNotifications);
            }
          });
        });
      }
    }
  }

  handleFetch(data: Observable<Notification[]>) {
    this.notifications$ = data;
    this.notifications$.subscribe(notifications => {
      const unreadNotifications = notifications.filter(notification => !notification.isRead)
      this.hasNoNotifications = unreadNotifications.length == 0;
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
