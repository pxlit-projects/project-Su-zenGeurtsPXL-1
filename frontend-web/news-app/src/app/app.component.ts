import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {NavbarComponent} from "./core/navbar/navbar.component";
import {PostListComponent} from "./core/posts/post-list/post-list.component";

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent, PostListComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'news-app';
}
