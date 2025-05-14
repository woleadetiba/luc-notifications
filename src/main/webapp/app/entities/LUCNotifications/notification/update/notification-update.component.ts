import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IDictionary } from 'app/entities/LUCNotifications/dictionary/dictionary.model';
import { DictionaryService } from 'app/entities/LUCNotifications/dictionary/service/dictionary.service';
import { INotification } from '../notification.model';
import { NotificationService } from '../service/notification.service';
import { NotificationFormGroup, NotificationFormService } from './notification-form.service';

@Component({
  selector: 'jhi-notification-update',
  templateUrl: './notification-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class NotificationUpdateComponent implements OnInit {
  isSaving = false;
  notification: INotification | null = null;

  statusesCollection: IDictionary[] = [];

  protected notificationService = inject(NotificationService);
  protected notificationFormService = inject(NotificationFormService);
  protected dictionaryService = inject(DictionaryService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: NotificationFormGroup = this.notificationFormService.createNotificationFormGroup();

  compareDictionary = (o1: IDictionary | null, o2: IDictionary | null): boolean => this.dictionaryService.compareDictionary(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ notification }) => {
      this.notification = notification;
      if (notification) {
        this.updateForm(notification);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const notification = this.notificationFormService.getNotification(this.editForm);
    if (notification.id !== null) {
      this.subscribeToSaveResponse(this.notificationService.update(notification));
    } else {
      this.subscribeToSaveResponse(this.notificationService.create(notification));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<INotification>>): void {
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

  protected updateForm(notification: INotification): void {
    this.notification = notification;
    this.notificationFormService.resetForm(this.editForm, notification);

    this.statusesCollection = this.dictionaryService.addDictionaryToCollectionIfMissing<IDictionary>(
      this.statusesCollection,
      notification.status,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.dictionaryService
      .query({ 'notificationId.specified': 'false' })
      .pipe(map((res: HttpResponse<IDictionary[]>) => res.body ?? []))
      .pipe(
        map((dictionaries: IDictionary[]) =>
          this.dictionaryService.addDictionaryToCollectionIfMissing<IDictionary>(dictionaries, this.notification?.status),
        ),
      )
      .subscribe((dictionaries: IDictionary[]) => (this.statusesCollection = dictionaries));
  }
}
