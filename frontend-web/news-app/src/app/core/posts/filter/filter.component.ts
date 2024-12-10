import {Component, EventEmitter, Output} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {Filter} from "../../../shared/models/filter.model";

@Component({
  selector: 'app-filter',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './filter.component.html',
  styles: [
  ]
})

export class FilterComponent {
  filter: Filter = { content: '', author: '', category: '' };

  @Output() filterChanged = new EventEmitter<Filter>();

  onSubmit(form: any) {
    if (form.valid) {
      this.filter.content = this.filter.content.toLowerCase();
      this.filter.author = this.filter.author.toLowerCase();
      this.filterChanged.emit(this.filter);
    }
  }

}

