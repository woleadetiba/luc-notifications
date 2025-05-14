import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { INotificationAttachment, NewNotificationAttachment } from '../notification-attachment.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts INotificationAttachment for edit and NewNotificationAttachmentFormGroupInput for create.
 */
type NotificationAttachmentFormGroupInput = INotificationAttachment | PartialWithRequiredKeyOf<NewNotificationAttachment>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends INotificationAttachment | NewNotificationAttachment> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

type NotificationAttachmentFormRawValue = FormValueOf<INotificationAttachment>;

type NewNotificationAttachmentFormRawValue = FormValueOf<NewNotificationAttachment>;

type NotificationAttachmentFormDefaults = Pick<NewNotificationAttachment, 'id' | 'createdAt'>;

type NotificationAttachmentFormGroupContent = {
  id: FormControl<NotificationAttachmentFormRawValue['id'] | NewNotificationAttachment['id']>;
  fileName: FormControl<NotificationAttachmentFormRawValue['fileName']>;
  fileType: FormControl<NotificationAttachmentFormRawValue['fileType']>;
  fileSize: FormControl<NotificationAttachmentFormRawValue['fileSize']>;
  filePath: FormControl<NotificationAttachmentFormRawValue['filePath']>;
  createdAt: FormControl<NotificationAttachmentFormRawValue['createdAt']>;
  notification: FormControl<NotificationAttachmentFormRawValue['notification']>;
};

export type NotificationAttachmentFormGroup = FormGroup<NotificationAttachmentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class NotificationAttachmentFormService {
  createNotificationAttachmentFormGroup(
    notificationAttachment: NotificationAttachmentFormGroupInput = { id: null },
  ): NotificationAttachmentFormGroup {
    const notificationAttachmentRawValue = this.convertNotificationAttachmentToNotificationAttachmentRawValue({
      ...this.getFormDefaults(),
      ...notificationAttachment,
    });
    return new FormGroup<NotificationAttachmentFormGroupContent>({
      id: new FormControl(
        { value: notificationAttachmentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      fileName: new FormControl(notificationAttachmentRawValue.fileName),
      fileType: new FormControl(notificationAttachmentRawValue.fileType),
      fileSize: new FormControl(notificationAttachmentRawValue.fileSize),
      filePath: new FormControl(notificationAttachmentRawValue.filePath),
      createdAt: new FormControl(notificationAttachmentRawValue.createdAt),
      notification: new FormControl(notificationAttachmentRawValue.notification),
    });
  }

  getNotificationAttachment(form: NotificationAttachmentFormGroup): INotificationAttachment | NewNotificationAttachment {
    return this.convertNotificationAttachmentRawValueToNotificationAttachment(
      form.getRawValue() as NotificationAttachmentFormRawValue | NewNotificationAttachmentFormRawValue,
    );
  }

  resetForm(form: NotificationAttachmentFormGroup, notificationAttachment: NotificationAttachmentFormGroupInput): void {
    const notificationAttachmentRawValue = this.convertNotificationAttachmentToNotificationAttachmentRawValue({
      ...this.getFormDefaults(),
      ...notificationAttachment,
    });
    form.reset(
      {
        ...notificationAttachmentRawValue,
        id: { value: notificationAttachmentRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): NotificationAttachmentFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdAt: currentTime,
    };
  }

  private convertNotificationAttachmentRawValueToNotificationAttachment(
    rawNotificationAttachment: NotificationAttachmentFormRawValue | NewNotificationAttachmentFormRawValue,
  ): INotificationAttachment | NewNotificationAttachment {
    return {
      ...rawNotificationAttachment,
      createdAt: dayjs(rawNotificationAttachment.createdAt, DATE_TIME_FORMAT),
    };
  }

  private convertNotificationAttachmentToNotificationAttachmentRawValue(
    notificationAttachment: INotificationAttachment | (Partial<NewNotificationAttachment> & NotificationAttachmentFormDefaults),
  ): NotificationAttachmentFormRawValue | PartialWithRequiredKeyOf<NewNotificationAttachmentFormRawValue> {
    return {
      ...notificationAttachment,
      createdAt: notificationAttachment.createdAt ? notificationAttachment.createdAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
