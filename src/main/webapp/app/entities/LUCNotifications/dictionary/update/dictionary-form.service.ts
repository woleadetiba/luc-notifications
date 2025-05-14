import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IDictionary, NewDictionary } from '../dictionary.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDictionary for edit and NewDictionaryFormGroupInput for create.
 */
type DictionaryFormGroupInput = IDictionary | PartialWithRequiredKeyOf<NewDictionary>;

type DictionaryFormDefaults = Pick<NewDictionary, 'id'>;

type DictionaryFormGroupContent = {
  id: FormControl<IDictionary['id'] | NewDictionary['id']>;
  keyName: FormControl<IDictionary['keyName']>;
  keyCode: FormControl<IDictionary['keyCode']>;
  label: FormControl<IDictionary['label']>;
  description: FormControl<IDictionary['description']>;
};

export type DictionaryFormGroup = FormGroup<DictionaryFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DictionaryFormService {
  createDictionaryFormGroup(dictionary: DictionaryFormGroupInput = { id: null }): DictionaryFormGroup {
    const dictionaryRawValue = {
      ...this.getFormDefaults(),
      ...dictionary,
    };
    return new FormGroup<DictionaryFormGroupContent>({
      id: new FormControl(
        { value: dictionaryRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      keyName: new FormControl(dictionaryRawValue.keyName),
      keyCode: new FormControl(dictionaryRawValue.keyCode),
      label: new FormControl(dictionaryRawValue.label),
      description: new FormControl(dictionaryRawValue.description),
    });
  }

  getDictionary(form: DictionaryFormGroup): IDictionary | NewDictionary {
    return form.getRawValue() as IDictionary | NewDictionary;
  }

  resetForm(form: DictionaryFormGroup, dictionary: DictionaryFormGroupInput): void {
    const dictionaryRawValue = { ...this.getFormDefaults(), ...dictionary };
    form.reset(
      {
        ...dictionaryRawValue,
        id: { value: dictionaryRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): DictionaryFormDefaults {
    return {
      id: null,
    };
  }
}
