import dayjs from 'dayjs/esm';

import { INotification, NewNotification } from './notification.model';

export const sampleWithRequiredData: INotification = {
  id: '4917d9fb-b873-41d6-981d-153319417462',
};

export const sampleWithPartialData: INotification = {
  id: '612703cb-4901-429d-9681-8ca4b101bfb4',
  subject: 'memorable boohoo',
  retryCount: 20975,
  maxRetries: 30960,
  errorMessage: 'bug',
  createdBy: 'voluntarily interior',
};

export const sampleWithFullData: INotification = {
  id: '27befbee-c69c-445f-a315-e759c182bfa5',
  recipientEmails: 'gust',
  ccEmails: 'opposite brand',
  subject: 'illusion meh phooey',
  messageBody: 'painfully far serpentine',
  retryCount: 10138,
  maxRetries: 11900,
  scheduledAt: dayjs('2025-05-14T03:49'),
  sentAt: dayjs('2025-05-14T00:45'),
  errorMessage: 'gown',
  createdAt: dayjs('2025-05-14T10:03'),
  createdBy: 'wherever bookend gallery',
};

export const sampleWithNewData: NewNotification = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
