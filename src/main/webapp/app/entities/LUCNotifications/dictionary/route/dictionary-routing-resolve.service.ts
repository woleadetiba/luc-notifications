import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDictionary } from '../dictionary.model';
import { DictionaryService } from '../service/dictionary.service';

const dictionaryResolve = (route: ActivatedRouteSnapshot): Observable<null | IDictionary> => {
  const id = route.params.id;
  if (id) {
    return inject(DictionaryService)
      .find(id)
      .pipe(
        mergeMap((dictionary: HttpResponse<IDictionary>) => {
          if (dictionary.body) {
            return of(dictionary.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default dictionaryResolve;
