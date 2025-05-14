import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../notification-attachment.test-samples';

import { NotificationAttachmentFormService } from './notification-attachment-form.service';

describe('NotificationAttachment Form Service', () => {
  let service: NotificationAttachmentFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NotificationAttachmentFormService);
  });

  describe('Service methods', () => {
    describe('createNotificationAttachmentFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createNotificationAttachmentFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fileName: expect.any(Object),
            fileType: expect.any(Object),
            fileSize: expect.any(Object),
            filePath: expect.any(Object),
            createdAt: expect.any(Object),
            notification: expect.any(Object),
          }),
        );
      });

      it('passing INotificationAttachment should create a new form with FormGroup', () => {
        const formGroup = service.createNotificationAttachmentFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fileName: expect.any(Object),
            fileType: expect.any(Object),
            fileSize: expect.any(Object),
            filePath: expect.any(Object),
            createdAt: expect.any(Object),
            notification: expect.any(Object),
          }),
        );
      });
    });

    describe('getNotificationAttachment', () => {
      it('should return NewNotificationAttachment for default NotificationAttachment initial value', () => {
        const formGroup = service.createNotificationAttachmentFormGroup(sampleWithNewData);

        const notificationAttachment = service.getNotificationAttachment(formGroup) as any;

        expect(notificationAttachment).toMatchObject(sampleWithNewData);
      });

      it('should return NewNotificationAttachment for empty NotificationAttachment initial value', () => {
        const formGroup = service.createNotificationAttachmentFormGroup();

        const notificationAttachment = service.getNotificationAttachment(formGroup) as any;

        expect(notificationAttachment).toMatchObject({});
      });

      it('should return INotificationAttachment', () => {
        const formGroup = service.createNotificationAttachmentFormGroup(sampleWithRequiredData);

        const notificationAttachment = service.getNotificationAttachment(formGroup) as any;

        expect(notificationAttachment).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing INotificationAttachment should not enable id FormControl', () => {
        const formGroup = service.createNotificationAttachmentFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewNotificationAttachment should disable id FormControl', () => {
        const formGroup = service.createNotificationAttachmentFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
