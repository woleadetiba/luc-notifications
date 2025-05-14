import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse, provideHttpClient } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subject, from, of } from 'rxjs';

import { INotification } from 'app/entities/LUCNotifications/notification/notification.model';
import { NotificationService } from 'app/entities/LUCNotifications/notification/service/notification.service';
import { NotificationAttachmentService } from '../service/notification-attachment.service';
import { INotificationAttachment } from '../notification-attachment.model';
import { NotificationAttachmentFormService } from './notification-attachment-form.service';

import { NotificationAttachmentUpdateComponent } from './notification-attachment-update.component';

describe('NotificationAttachment Management Update Component', () => {
  let comp: NotificationAttachmentUpdateComponent;
  let fixture: ComponentFixture<NotificationAttachmentUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let notificationAttachmentFormService: NotificationAttachmentFormService;
  let notificationAttachmentService: NotificationAttachmentService;
  let notificationService: NotificationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [NotificationAttachmentUpdateComponent],
      providers: [
        provideHttpClient(),
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(NotificationAttachmentUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(NotificationAttachmentUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    notificationAttachmentFormService = TestBed.inject(NotificationAttachmentFormService);
    notificationAttachmentService = TestBed.inject(NotificationAttachmentService);
    notificationService = TestBed.inject(NotificationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Notification query and add missing value', () => {
      const notificationAttachment: INotificationAttachment = { id: '149ab936-c806-4b24-802c-cab259b50e63' };
      const notification: INotification = { id: '77fa88cf-0751-486e-b40c-615afa341742' };
      notificationAttachment.notification = notification;

      const notificationCollection: INotification[] = [{ id: '77fa88cf-0751-486e-b40c-615afa341742' }];
      jest.spyOn(notificationService, 'query').mockReturnValue(of(new HttpResponse({ body: notificationCollection })));
      const additionalNotifications = [notification];
      const expectedCollection: INotification[] = [...additionalNotifications, ...notificationCollection];
      jest.spyOn(notificationService, 'addNotificationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ notificationAttachment });
      comp.ngOnInit();

      expect(notificationService.query).toHaveBeenCalled();
      expect(notificationService.addNotificationToCollectionIfMissing).toHaveBeenCalledWith(
        notificationCollection,
        ...additionalNotifications.map(expect.objectContaining),
      );
      expect(comp.notificationsSharedCollection).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const notificationAttachment: INotificationAttachment = { id: '149ab936-c806-4b24-802c-cab259b50e63' };
      const notification: INotification = { id: '77fa88cf-0751-486e-b40c-615afa341742' };
      notificationAttachment.notification = notification;

      activatedRoute.data = of({ notificationAttachment });
      comp.ngOnInit();

      expect(comp.notificationsSharedCollection).toContainEqual(notification);
      expect(comp.notificationAttachment).toEqual(notificationAttachment);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<INotificationAttachment>>();
      const notificationAttachment = { id: '48dceab7-3a17-4181-86ca-670bfc1a30cb' };
      jest.spyOn(notificationAttachmentFormService, 'getNotificationAttachment').mockReturnValue(notificationAttachment);
      jest.spyOn(notificationAttachmentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ notificationAttachment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: notificationAttachment }));
      saveSubject.complete();

      // THEN
      expect(notificationAttachmentFormService.getNotificationAttachment).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(notificationAttachmentService.update).toHaveBeenCalledWith(expect.objectContaining(notificationAttachment));
      expect(comp.isSaving).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<INotificationAttachment>>();
      const notificationAttachment = { id: '48dceab7-3a17-4181-86ca-670bfc1a30cb' };
      jest.spyOn(notificationAttachmentFormService, 'getNotificationAttachment').mockReturnValue({ id: null });
      jest.spyOn(notificationAttachmentService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ notificationAttachment: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: notificationAttachment }));
      saveSubject.complete();

      // THEN
      expect(notificationAttachmentFormService.getNotificationAttachment).toHaveBeenCalled();
      expect(notificationAttachmentService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<INotificationAttachment>>();
      const notificationAttachment = { id: '48dceab7-3a17-4181-86ca-670bfc1a30cb' };
      jest.spyOn(notificationAttachmentService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ notificationAttachment });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(notificationAttachmentService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareNotification', () => {
      it('should forward to notificationService', () => {
        const entity = { id: '77fa88cf-0751-486e-b40c-615afa341742' };
        const entity2 = { id: '99402b2a-658f-492c-b166-d21ab78286d1' };
        jest.spyOn(notificationService, 'compareNotification');
        comp.compareNotification(entity, entity2);
        expect(notificationService.compareNotification).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
