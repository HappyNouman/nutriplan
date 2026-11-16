import { DatePipe } from '@angular/common';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { errorMessage } from '../../core/http-errors';
import { CATEGORY_LABELS, ShoppingItem, ShoppingList, UNIT_LABELS } from './shopping.models';
import { ShoppingService } from './shopping.service';

@Component({
  selector: 'app-shopping',
  imports: [RouterLink, DatePipe],
  templateUrl: './shopping.html',
  styleUrl: './shopping.css',
})
export class Shopping implements OnInit {
  private readonly shoppingService = inject(ShoppingService);

  protected readonly categoryLabels = CATEGORY_LABELS;
  protected readonly unitLabels = UNIT_LABELS;

  protected readonly list = signal<ShoppingList | null>(null);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);

  /** How full the progress bar is: checked items out of all items, in %. */
  protected readonly progress = computed(() => {
    const list = this.list();
    if (!list || list.totalItems === 0) {
      return 0;
    }
    return Math.round((list.checkedItems / list.totalItems) * 100);
  });

  ngOnInit(): void {
    this.load();
  }

  /** Tick or untick an item, then reload the list so the numbers update. */
  protected toggle(item: ShoppingItem): void {
    this.error.set(null);
    this.shoppingService.setChecked(item.key, !item.checked).subscribe({
      next: () => this.load(),
      error: (err) => this.error.set(errorMessage(err)),
    });
  }

  private load(): void {
    this.shoppingService.current().subscribe({
      next: (list) => {
        this.list.set(list);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(errorMessage(err));
        this.loading.set(false);
      },
    });
  }
}
