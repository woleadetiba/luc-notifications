import { Component, input } from '@angular/core';
import { RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { INotificationAttachment } from '../notification-attachment.model';

@Component({
  selector: 'jhi-notification-attachment-detail',
  templateUrl: './notification-attachment-detail.component.html',
  imports: [SharedModule, RouterModule, FormatMediumDatetimePipe],
})
export class NotificationAttachmentDetailComponent {
  notificationAttachment = input<INotificationAttachment | null>(null);

  previousState(): void {
    window.history.back();
  }
}
