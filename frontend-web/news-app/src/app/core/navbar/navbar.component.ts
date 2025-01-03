import {Component, inject, OnDestroy, OnInit} from '@angular/core';
import {Router, RouterLink, RouterLinkActive} from "@angular/router";
import {AsyncPipe, NgOptimizedImage} from "@angular/common";
import {AuthenticationService} from "../../shared/services/authentication.service";
import {PostService} from "../../shared/services/post.service";
import {Notification} from "../../shared/models/notification.model";
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
    let fetchedNotifications = this.postService.getNotifications();

    if (this.notifications$ === undefined) {
      this.notifications$ = this.postService.getNotifications();
      this.notifications$.subscribe(notifications => {
        this.hasNoNotifications = notifications.length == 0;
      });
    } else {
      fetchedNotifications.subscribe(fetchedNotificationsData => {
        this.notifications$.subscribe(notifications => {
          this.hasNoNotifications = notifications.length == 0;
          if (fetchedNotificationsData == notifications) {
            this.notifications$ = this.postService.getNotifications();
          }
        });
      });
    }
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

  openPost(id: number) {
    this.router.navigate(['/myPost/' + id]);
    this.userMenuIsHidden = true;
    this.notificationsMenuIsHidden = true;
  }
}
