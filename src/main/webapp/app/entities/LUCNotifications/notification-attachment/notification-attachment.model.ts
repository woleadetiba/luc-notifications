import dayjs from 'dayjs/esm';
import { INotification } from 'app/entities/LUCNotifications/notification/notification.model';

export interface INotificationAttachment {
  id: string;
  fileName?: string | null;
  fileType?: string | null;
  fileSize?: number | null;
  filePath?: string | null;
  createdAt?: dayjs.Dayjs | null;
  notification?: Pick<INotification, 'id'> | null;
}

export type NewNotificationAttachment = Omit<INotificationAttachment, 'id'> & { id: null };
