import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { INotification } from 'app/entities/LUCNotifications/notification/notification.model';
import { NotificationService } from 'app/entities/LUCNotifications/notification/service/notification.service';
import { INotificationAttachment } from '../notification-attachment.model';
import { NotificationAttachmentService } from '../service/notification-attachment.service';
import { NotificationAttachmentFormGroup, NotificationAttachmentFormService } from './notification-attachment-form.service';

@Component({
  selector: 'jhi-notification-attachment-update',
  templateUrl: './notification-attachment-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class NotificationAttachmentUpdateComponent implements OnInit {
  isSaving = false;
  notificationAttachment: INotificationAttachment | null = null;

  notificationsSharedCollection: INotification[] = [];

  protected notificationAttachmentService = inject(NotificationAttachmentService);
  protected notificationAttachmentFormService = inject(NotificationAttachmentFormService);
  protected notificationService = inject(NotificationService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: NotificationAttachmentFormGroup = this.notificationAttachmentFormService.createNotificationAttachmentFormGroup();

  compareNotification = (o1: INotification | null, o2: INotification | null): boolean =>
    this.notificationService.compareNotification(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ notificationAttachment }) => {
      this.notificationAttachment = notificationAttachment;
      if (notificationAttachment) {
        this.updateForm(notificationAttachment);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const notificationAttachment = this.notificationAttachmentFormService.getNotificationAttachment(this.editForm);
    if (notificationAttachment.id !== null) {
      this.subscribeToSaveResponse(this.notificationAttachmentService.update(notificationAttachment));
    } else {
      this.subscribeToSaveResponse(this.notificationAttachmentService.create(notificationAttachment));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<INotificationAttachment>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(notificationAttachment: INotificationAttachment): void {
    this.notificationAttachment = notificationAttachment;
    this.notificationAttachmentFormService.resetForm(this.editForm, notificationAttachment);

    this.notificationsSharedCollection = this.notificationService.addNotificationToCollectionIfMissing<INotification>(
      this.notificationsSharedCollection,
      notificationAttachment.notification,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.notificationService
      .query()
      .pipe(map((res: HttpResponse<INotification[]>) => res.body ?? []))
      .pipe(
        map((notifications: INotification[]) =>
          this.notificationService.addNotificationToCollectionIfMissing<INotification>(
            notifications,
            this.notificationAttachment?.notification,
          ),
        ),
      )
      .subscribe((notifications: INotification[]) => (this.notificationsSharedCollection = notifications));
  }
}
