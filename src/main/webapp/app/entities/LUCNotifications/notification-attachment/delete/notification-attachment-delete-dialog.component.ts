import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { INotificationAttachment } from '../notification-attachment.model';
import { NotificationAttachmentService } from '../service/notification-attachment.service';

@Component({
  templateUrl: './notification-attachment-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class NotificationAttachmentDeleteDialogComponent {
  notificationAttachment?: INotificationAttachment;

  protected notificationAttachmentService = inject(NotificationAttachmentService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.notificationAttachmentService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
