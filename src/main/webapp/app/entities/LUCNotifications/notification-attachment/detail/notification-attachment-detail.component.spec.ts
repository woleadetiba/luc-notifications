import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { NotificationAttachmentDetailComponent } from './notification-attachment-detail.component';

describe('NotificationAttachment Management Detail Component', () => {
  let comp: NotificationAttachmentDetailComponent;
  let fixture: ComponentFixture<NotificationAttachmentDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NotificationAttachmentDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              loadComponent: () => import('./notification-attachment-detail.component').then(m => m.NotificationAttachmentDetailComponent),
              resolve: { notificationAttachment: () => of({ id: '48dceab7-3a17-4181-86ca-670bfc1a30cb' }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(NotificationAttachmentDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NotificationAttachmentDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('should load notificationAttachment on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', NotificationAttachmentDetailComponent);

      // THEN
      expect(instance.notificationAttachment()).toEqual(expect.objectContaining({ id: '48dceab7-3a17-4181-86ca-670bfc1a30cb' }));
    });
  });

  describe('PreviousState', () => {
    it('should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
