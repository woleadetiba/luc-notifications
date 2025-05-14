import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { INotification, NewNotification } from '../notification.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts INotification for edit and NewNotificationFormGroupInput for create.
 */
type NotificationFormGroupInput = INotification | PartialWithRequiredKeyOf<NewNotification>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends INotification | NewNotification> = Omit<T, 'scheduledAt' | 'sentAt' | 'createdAt'> & {
  scheduledAt?: string | null;
  sentAt?: string | null;
  createdAt?: string | null;
};

type NotificationFormRawValue = FormValueOf<INotification>;

type NewNotificationFormRawValue = FormValueOf<NewNotification>;

type NotificationFormDefaults = Pick<NewNotification, 'id' | 'scheduledAt' | 'sentAt' | 'createdAt'>;

type NotificationFormGroupContent = {
  id: FormControl<NotificationFormRawValue['id'] | NewNotification['id']>;
  recipientEmails: FormControl<NotificationFormRawValue['recipientEmails']>;
  ccEmails: FormControl<NotificationFormRawValue['ccEmails']>;
  subject: FormControl<NotificationFormRawValue['subject']>;
  messageBody: FormControl<NotificationFormRawValue['messageBody']>;
  retryCount: FormControl<NotificationFormRawValue['retryCount']>;
  maxRetries: FormControl<NotificationFormRawValue['maxRetries']>;
  scheduledAt: FormControl<NotificationFormRawValue['scheduledAt']>;
  sentAt: FormControl<NotificationFormRawValue['sentAt']>;
  errorMessage: FormControl<NotificationFormRawValue['errorMessage']>;
  createdAt: FormControl<NotificationFormRawValue['createdAt']>;
  createdBy: FormControl<NotificationFormRawValue['createdBy']>;
  status: FormControl<NotificationFormRawValue['status']>;
};

export type NotificationFormGroup = FormGroup<NotificationFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class NotificationFormService {
  createNotificationFormGroup(notification: NotificationFormGroupInput = { id: null }): NotificationFormGroup {
    const notificationRawValue = this.convertNotificationToNotificationRawValue({
      ...this.getFormDefaults(),
      ...notification,
    });
    return new FormGroup<NotificationFormGroupContent>({
      id: new FormControl(
        { value: notificationRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      recipientEmails: new FormControl(notificationRawValue.recipientEmails),
      ccEmails: new FormControl(notificationRawValue.ccEmails),
      subject: new FormControl(notificationRawValue.subject),
      messageBody: new FormControl(notificationRawValue.messageBody),
      retryCount: new FormControl(notificationRawValue.retryCount),
      maxRetries: new FormControl(notificationRawValue.maxRetries),
      scheduledAt: new FormControl(notificationRawValue.scheduledAt),
      sentAt: new FormControl(notificationRawValue.sentAt),
      errorMessage: new FormControl(notificationRawValue.errorMessage),
      createdAt: new FormControl(notificationRawValue.createdAt),
      createdBy: new FormControl(notificationRawValue.createdBy),
      status: new FormControl(notificationRawValue.status),
    });
  }

  getNotification(form: NotificationFormGroup): INotification | NewNotification {
    return this.convertNotificationRawValueToNotification(form.getRawValue() as NotificationFormRawValue | NewNotificationFormRawValue);
  }

  resetForm(form: NotificationFormGroup, notification: NotificationFormGroupInput): void {
    const notificationRawValue = this.convertNotificationToNotificationRawValue({ ...this.getFormDefaults(), ...notification });
    form.reset(
      {
        ...notificationRawValue,
        id: { value: notificationRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): NotificationFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      scheduledAt: currentTime,
      sentAt: currentTime,
      createdAt: currentTime,
    };
  }

  private convertNotificationRawValueToNotification(
    rawNotification: NotificationFormRawValue | NewNotificationFormRawValue,
  ): INotification | NewNotification {
    return {
      ...rawNotification,
      scheduledAt: dayjs(rawNotification.scheduledAt, DATE_TIME_FORMAT),
      sentAt: dayjs(rawNotification.sentAt, DATE_TIME_FORMAT),
      createdAt: dayjs(rawNotification.createdAt, DATE_TIME_FORMAT),
    };
  }

  private convertNotificationToNotificationRawValue(
    notification: INotification | (Partial<NewNotification> & NotificationFormDefaults),
  ): NotificationFormRawValue | PartialWithRequiredKeyOf<NewNotificationFormRawValue> {
    return {
      ...notification,
      scheduledAt: notification.scheduledAt ? notification.scheduledAt.format(DATE_TIME_FORMAT) : undefined,
      sentAt: notification.sentAt ? notification.sentAt.format(DATE_TIME_FORMAT) : undefined,
      createdAt: notification.createdAt ? notification.createdAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
