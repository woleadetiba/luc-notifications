import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IDictionary, NewDictionary } from '../dictionary.model';

export type PartialUpdateDictionary = Partial<IDictionary> & Pick<IDictionary, 'id'>;

export type EntityResponseType = HttpResponse<IDictionary>;
export type EntityArrayResponseType = HttpResponse<IDictionary[]>;

@Injectable({ providedIn: 'root' })
export class DictionaryService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/dictionaries', 'lucnotifications');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/dictionaries/_search', 'lucnotifications');

  create(dictionary: NewDictionary): Observable<EntityResponseType> {
    return this.http.post<IDictionary>(this.resourceUrl, dictionary, { observe: 'response' });
  }

  update(dictionary: IDictionary): Observable<EntityResponseType> {
    return this.http.put<IDictionary>(`${this.resourceUrl}/${this.getDictionaryIdentifier(dictionary)}`, dictionary, {
      observe: 'response',
    });
  }

  partialUpdate(dictionary: PartialUpdateDictionary): Observable<EntityResponseType> {
    return this.http.patch<IDictionary>(`${this.resourceUrl}/${this.getDictionaryIdentifier(dictionary)}`, dictionary, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IDictionary>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDictionary[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IDictionary[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<IDictionary[]>()], asapScheduler)));
  }

  getDictionaryIdentifier(dictionary: Pick<IDictionary, 'id'>): number {
    return dictionary.id;
  }

  compareDictionary(o1: Pick<IDictionary, 'id'> | null, o2: Pick<IDictionary, 'id'> | null): boolean {
    return o1 && o2 ? this.getDictionaryIdentifier(o1) === this.getDictionaryIdentifier(o2) : o1 === o2;
  }

  addDictionaryToCollectionIfMissing<Type extends Pick<IDictionary, 'id'>>(
    dictionaryCollection: Type[],
    ...dictionariesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const dictionaries: Type[] = dictionariesToCheck.filter(isPresent);
    if (dictionaries.length > 0) {
      const dictionaryCollectionIdentifiers = dictionaryCollection.map(dictionaryItem => this.getDictionaryIdentifier(dictionaryItem));
      const dictionariesToAdd = dictionaries.filter(dictionaryItem => {
        const dictionaryIdentifier = this.getDictionaryIdentifier(dictionaryItem);
        if (dictionaryCollectionIdentifiers.includes(dictionaryIdentifier)) {
          return false;
        }
        dictionaryCollectionIdentifiers.push(dictionaryIdentifier);
        return true;
      });
      return [...dictionariesToAdd, ...dictionaryCollection];
    }
    return dictionaryCollection;
  }
}
