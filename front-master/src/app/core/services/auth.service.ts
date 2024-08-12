import { Injectable } from '@angular/core';

// 实现某一功能的逻辑单元
// 当你在一个类前面添加 @Injectable() 装饰器，你就告诉 Angular 这个类可以被用作提供器，也就是说，Angular 可以使用这个类来创建一个实例，并将其注入到其他需要它的类中。
// 提供器：首先，你需要理解的是，Angular 中的每个服务类或可注入类都被视为一个 "提供器"。它们提供（或创建）一些我们在应用程序其他部分需要的东西，例如数据访问服务或用户身份验证服务。
// 依赖注入：Angular 的依赖注入（DI）系统允许我们将这些提供器注入到需要它们的组件或其他服务中。这意味着我们不需要在每个组件或服务中手动创建这些类的新实例，我们只需告诉 Angular 我们需要它们，Angular 就会为我们创建并提供它们。
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  permissions: string[] | null = null;
  code: string | null = null;

  constructor() { }

  setAuth(permissions: string[], code: string) {
    this.permissions = permissions;
    this.code = code;
  }

  setPermission(permissions: string[]) {
    this.permissions = permissions;
  }

  setCode(code: string) {
    this.code = code;
  }

  check(): boolean { ///todo
    if (this.permissions && this.code)
    {
      console.log('check if you have permission');
      return true;
    }
    return false;
  }
}