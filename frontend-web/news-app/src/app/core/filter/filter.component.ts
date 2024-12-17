import {Component, EventEmitter, inject, Input, Output} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {Filter} from "../../shared/models/filter.model";
import {Router} from "@angular/router";

@Component({
  selector: 'app-filter',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './filter.component.html',
  styles: [
  ]
})

export class FilterComponent {
  router: Router = inject(Router);
  @Input() mine!: boolean;
  filter: Filter = { content: '', author: '', date: '' };
  userRole$: string | null = localStorage.getItem('userRole');

  @Output() filterChanged = new EventEmitter<Filter>();

  onSubmit(form: any) {
    if (form.valid) {
      this.filterChanged.emit(this.filter);
    }
  }

  addPost() {
    this.router.navigate(['/addPost']);
  }

}

