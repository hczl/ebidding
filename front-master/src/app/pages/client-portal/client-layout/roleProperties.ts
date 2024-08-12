export interface MenuItem {
  path: string,
  desc: string,
  icon: string
}

export const ClientMenu: MenuItem[] = [
  {
    path: '/client/homepage',
    desc: 'Client Dashboard',
    icon: 'dashboard'
  },
  {
    path: '/client/bidding',
    desc: 'Bidding',
    icon: 'paid'
  },
  {
    path: '/client/history',
    desc: 'History',
    icon: 'history'
  }
];
  
export const TraderMenu: MenuItem[] = [
  {
    path: '/trader/homepage',
    desc: 'Manager Dashboard',
    icon: 'dashboard'
  },
  {
    path: '/trader/admin',
    desc: 'Admin',
    icon: 'manage_accounts'
  },
  {
    path: '/trader/bond',
    desc: 'Bond',
    icon: 'local_atm'
  }
];


export const avatarUrl = {
  trader: 'https://s2.loli.net/2023/07/13/1GoKAnSPcMCkr6X.png',
  client: 'https://s2.loli.net/2023/07/13/fFXMyzvNq2TBpu3.png'

}
