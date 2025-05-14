import dayjs from 'dayjs/esm';
import { IDictionary } from 'app/entities/LUCNotifications/dictionary/dictionary.model';

export interface INotification {
  id: string;
  recipientEmails?: string | null;
  ccEmails?: string | null;
  subject?: string | null;
  messageBody?: string | null;
  retryCount?: number | null;
  maxRetries?: number | null;
  scheduledAt?: dayjs.Dayjs | null;
  sentAt?: dayjs.Dayjs | null;
  errorMessage?: string | null;
  createdAt?: dayjs.Dayjs | null;
  createdBy?: string | null;
  status?: Pick<IDictionary, 'id'> | null;
}

export type NewNotification = Omit<INotification, 'id'> & { id: null };
