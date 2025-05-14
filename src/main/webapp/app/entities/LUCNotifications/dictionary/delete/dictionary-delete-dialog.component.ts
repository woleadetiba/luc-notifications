import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IDictionary } from '../dictionary.model';
import { DictionaryService } from '../service/dictionary.service';

@Component({
  templateUrl: './dictionary-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class DictionaryDeleteDialogComponent {
  dictionary?: IDictionary;

  protected dictionaryService = inject(DictionaryService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.dictionaryService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
