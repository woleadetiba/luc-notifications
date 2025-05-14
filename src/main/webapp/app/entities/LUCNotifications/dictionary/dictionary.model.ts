export interface IDictionary {
  id: number;
  keyName?: string | null;
  keyCode?: string | null;
  label?: string | null;
  description?: string | null;
}

export type NewDictionary = Omit<IDictionary, 'id'> & { id: null };
