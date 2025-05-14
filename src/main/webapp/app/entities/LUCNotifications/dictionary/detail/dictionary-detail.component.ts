import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { IDictionary } from '../dictionary.model';

@Component({
  selector: 'jhi-dictionary-detail',
  templateUrl: './dictionary-detail.component.html',
  imports: [SharedModule, RouterModule],
})
export class DictionaryDetailComponent {
  dictionary = input<IDictionary | null>(null);

  previousState(): void {
    window.history.back();
  }
}
