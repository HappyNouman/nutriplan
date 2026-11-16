// Mirrors the backend's ShoppingListResponse.

export type IngredientCategory = 'PRODUCE' | 'PROTEIN' | 'DAIRY' | 'GRAINS' | 'PANTRY';
export type IngredientUnit = 'GRAM' | 'ML' | 'PIECE';

export interface ShoppingItem {
  key: string; // e.g. "eggs:piece", sent back to tick the item
  name: string;
  category: IngredientCategory;
  unit: IngredientUnit;
  quantity: number;
  checked: boolean;
}

export interface ShoppingSection {
  category: IngredientCategory;
  items: ShoppingItem[];
}

export interface ShoppingList {
  weekStart: string;
  totalItems: number;
  checkedItems: number;
  sections: ShoppingSection[];
}

export const CATEGORY_LABELS: Record<IngredientCategory, string> = {
  PRODUCE: 'Fruit & vegetables',
  PROTEIN: 'Meat, fish & eggs',
  DAIRY: 'Dairy',
  GRAINS: 'Bread & grains',
  PANTRY: 'Pantry',
};

/** Shown after the number: "750 g", "330 ml", "14" (pieces need no unit). */
export const UNIT_LABELS: Record<IngredientUnit, string> = {
  GRAM: 'g',
  ML: 'ml',
  PIECE: '',
};
