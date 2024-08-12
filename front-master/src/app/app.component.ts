import { Component, DestroyRef, OnDestroy, OnInit, inject } from '@angular/core';
import { SpinService } from './core/services/spin.service';
import { NavigationEnd, Router } from '@angular/router';
import { NzSafeAny } from 'ng-zorro-antd/core/types';
import { filter } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';



@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.less'],
})
export class AppComponent implements OnInit{
  constructor(private spinService: SpinService, public router: Router) {}

  loading$ = this.spinService.getCurrentGlobalSpinStore();
  destroyRef = inject(DestroyRef);

  ngOnInit(): void {
    this.router.events
    .pipe(
      filter((event: NzSafeAny) => event instanceof NavigationEnd),
      takeUntilDestroyed(this.destroyRef)
    )
    .subscribe(() => {
      this.spinService.setCurrentGlobalSpinStore(false);
    });
  }

}
