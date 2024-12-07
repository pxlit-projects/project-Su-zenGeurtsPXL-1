import {Component} from "@angular/core";
import {RouterLink, RouterLinkActive} from "@angular/router";
import {NgOptimizedImage} from "@angular/common";

@Component({
  selector: 'app-page-not-found',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, NgOptimizedImage],
  templateUrl: './page-not-found.component.html',
  styleUrl: './page-not-found.component.css'
})
export class PageNotFoundComponent {

}
