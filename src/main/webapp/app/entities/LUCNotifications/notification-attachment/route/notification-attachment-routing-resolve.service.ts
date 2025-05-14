import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { INotificationAttachment } from '../notification-attachment.model';
import { NotificationAttachmentService } from '../service/notification-attachment.service';

const notificationAttachmentResolve = (route: ActivatedRouteSnapshot): Observable<null | INotificationAttachment> => {
  const id = route.params.id;
  if (id) {
    return inject(NotificationAttachmentService)
      .find(id)
      .pipe(
        mergeMap((notificationAttachment: HttpResponse<INotificationAttachment>) => {
          if (notificationAttachment.body) {
            return of(notificationAttachment.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default notificationAttachmentResolve;
