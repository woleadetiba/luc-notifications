import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, map, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { INotificationAttachment, NewNotificationAttachment } from '../notification-attachment.model';

export type PartialUpdateNotificationAttachment = Partial<INotificationAttachment> & Pick<INotificationAttachment, 'id'>;

type RestOf<T extends INotificationAttachment | NewNotificationAttachment> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

export type RestNotificationAttachment = RestOf<INotificationAttachment>;

export type NewRestNotificationAttachment = RestOf<NewNotificationAttachment>;

export type PartialUpdateRestNotificationAttachment = RestOf<PartialUpdateNotificationAttachment>;

export type EntityResponseType = HttpResponse<INotificationAttachment>;
export type EntityArrayResponseType = HttpResponse<INotificationAttachment[]>;

@Injectable({ providedIn: 'root' })
export class NotificationAttachmentService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/notification-attachments', 'lucnotifications');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/notification-attachments/_search', 'lucnotifications');

  create(notificationAttachment: NewNotificationAttachment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(notificationAttachment);
    return this.http
      .post<RestNotificationAttachment>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(notificationAttachment: INotificationAttachment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(notificationAttachment);
    return this.http
      .put<RestNotificationAttachment>(`${this.resourceUrl}/${this.getNotificationAttachmentIdentifier(notificationAttachment)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(notificationAttachment: PartialUpdateNotificationAttachment): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(notificationAttachment);
    return this.http
      .patch<RestNotificationAttachment>(`${this.resourceUrl}/${this.getNotificationAttachmentIdentifier(notificationAttachment)}`, copy, {
        observe: 'response',
      })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http
      .get<RestNotificationAttachment>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestNotificationAttachment[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: string): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<RestNotificationAttachment[]>(this.resourceSearchUrl, { params: options, observe: 'response' }).pipe(
      map(res => this.convertResponseArrayFromServer(res)),

      catchError(() => scheduled([new HttpResponse<INotificationAttachment[]>()], asapScheduler)),
    );
  }

  getNotificationAttachmentIdentifier(notificationAttachment: Pick<INotificationAttachment, 'id'>): string {
    return notificationAttachment.id;
  }

  compareNotificationAttachment(o1: Pick<INotificationAttachment, 'id'> | null, o2: Pick<INotificationAttachment, 'id'> | null): boolean {
    return o1 && o2 ? this.getNotificationAttachmentIdentifier(o1) === this.getNotificationAttachmentIdentifier(o2) : o1 === o2;
  }

  addNotificationAttachmentToCollectionIfMissing<Type extends Pick<INotificationAttachment, 'id'>>(
    notificationAttachmentCollection: Type[],
    ...notificationAttachmentsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const notificationAttachments: Type[] = notificationAttachmentsToCheck.filter(isPresent);
    if (notificationAttachments.length > 0) {
      const notificationAttachmentCollectionIdentifiers = notificationAttachmentCollection.map(notificationAttachmentItem =>
        this.getNotificationAttachmentIdentifier(notificationAttachmentItem),
      );
      const notificationAttachmentsToAdd = notificationAttachments.filter(notificationAttachmentItem => {
        const notificationAttachmentIdentifier = this.getNotificationAttachmentIdentifier(notificationAttachmentItem);
        if (notificationAttachmentCollectionIdentifiers.includes(notificationAttachmentIdentifier)) {
          return false;
        }
        notificationAttachmentCollectionIdentifiers.push(notificationAttachmentIdentifier);
        return true;
      });
      return [...notificationAttachmentsToAdd, ...notificationAttachmentCollection];
    }
    return notificationAttachmentCollection;
  }

  protected convertDateFromClient<T extends INotificationAttachment | NewNotificationAttachment | PartialUpdateNotificationAttachment>(
    notificationAttachment: T,
  ): RestOf<T> {
    return {
      ...notificationAttachment,
      createdAt: notificationAttachment.createdAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restNotificationAttachment: RestNotificationAttachment): INotificationAttachment {
    return {
      ...restNotificationAttachment,
      createdAt: restNotificationAttachment.createdAt ? dayjs(restNotificationAttachment.createdAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestNotificationAttachment>): HttpResponse<INotificationAttachment> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestNotificationAttachment[]>): HttpResponse<INotificationAttachment[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
