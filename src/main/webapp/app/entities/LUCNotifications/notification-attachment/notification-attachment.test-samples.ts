import dayjs from 'dayjs/esm';

import { INotificationAttachment, NewNotificationAttachment } from './notification-attachment.model';

export const sampleWithRequiredData: INotificationAttachment = {
  id: 'af6ed701-fd0a-47df-a09e-7d4a75043b2f',
};

export const sampleWithPartialData: INotificationAttachment = {
  id: 'b998d0d3-70c5-468d-819c-4d8987d1097a',
  fileName: 'phooey',
  fileType: 'spotless ruin toward',
  fileSize: 19619,
};

export const sampleWithFullData: INotificationAttachment = {
  id: '9d9b6edb-c401-42cb-bbb9-f107bbdf3cad',
  fileName: 'ick dial stealthily',
  fileType: 'though a',
  fileSize: 26592,
  filePath: 'encouragement now',
  createdAt: dayjs('2025-05-13T23:54'),
};

export const sampleWithNewData: NewNotificationAttachment = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
