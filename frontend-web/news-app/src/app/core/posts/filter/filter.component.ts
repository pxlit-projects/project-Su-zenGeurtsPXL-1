import {Component, EventEmitter, Input, Output} from '@angular/core';
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
  @Input() mine!: boolean;
  filter: Filter = { content: '', author: '', category: '' };

  @Output() filterChanged = new EventEmitter<Filter>();

  onSubmit(form: any) {
    if (form.valid) {
      this.filterChanged.emit(this.filter);
    }
  }

}

