import { Component, OnInit, inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IDictionary } from '../dictionary.model';
import { DictionaryService } from '../service/dictionary.service';
import { DictionaryFormGroup, DictionaryFormService } from './dictionary-form.service';

@Component({
  selector: 'jhi-dictionary-update',
  templateUrl: './dictionary-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class DictionaryUpdateComponent implements OnInit {
  isSaving = false;
  dictionary: IDictionary | null = null;

  protected dictionaryService = inject(DictionaryService);
  protected dictionaryFormService = inject(DictionaryFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: DictionaryFormGroup = this.dictionaryFormService.createDictionaryFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ dictionary }) => {
      this.dictionary = dictionary;
      if (dictionary) {
        this.updateForm(dictionary);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const dictionary = this.dictionaryFormService.getDictionary(this.editForm);
    if (dictionary.id !== null) {
      this.subscribeToSaveResponse(this.dictionaryService.update(dictionary));
    } else {
      this.subscribeToSaveResponse(this.dictionaryService.create(dictionary));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDictionary>>): void {
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

  protected updateForm(dictionary: IDictionary): void {
    this.dictionary = dictionary;
    this.dictionaryFormService.resetForm(this.editForm, dictionary);
  }
}
