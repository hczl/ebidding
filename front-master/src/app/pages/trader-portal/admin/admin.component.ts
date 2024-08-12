import {Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {BwicService} from 'src/app/core/services/bwic.service';
import {NzTabsModule} from 'ng-zorro-antd/tabs';
import {NzIconModule} from 'ng-zorro-antd/icon';
import {OngoingTableComponent} from './ongoing-table/ongoing-table.component';
import {UpcomingTableComponent} from './upcoming-table/upcoming-table.component';
import {EndedTableComponent} from './ended-table/ended-table.component';
import {MatCardModule} from '@angular/material/card';
import {MatDividerModule} from '@angular/material/divider';
import {MatIconModule} from '@angular/material/icon';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {MatInputModule} from '@angular/material/input';
import {HttpClient} from '@angular/common/http';
import {GptService} from 'src/app/core/services/gpt.service';
import {ProgressBarMode} from '@angular/material/progress-bar';
import {Subscription} from 'rxjs';
import {MatTabsModule} from '@angular/material/tabs';




@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [
    CommonModule,
    NzTabsModule,
    NzIconModule,
    OngoingTableComponent,
    UpcomingTableComponent,
    EndedTableComponent,
    MatCardModule,
    MatDividerModule,
    MatIconModule,
    MatProgressBarModule,
    MatInputModule,
    MatTabsModule
  ],
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.less']
})
export class AdminComponent {

  selectedIndex=0;

  ongoingData: any[]=[];
  upcomingData: any[]=[];
  endedData: any[]=[];
  gptResponse: string='';
  message: string='';
  hover: boolean=false;

  defaultIconType: string='sentiment_very_satisfied';  // default icon
  queryIconType: string='auto_mode';  // query icon
  defaultProgressBarMode: ProgressBarMode='determinate';
  queryProgressBarMode: ProgressBarMode='query';

  // Set initial iconType and progressBarMode to default
  submitIconType: string=this.defaultIconType;
  progressBarMode: ProgressBarMode=this.defaultProgressBarMode;

  private subscription!: Subscription;


  constructor(private bwicService: BwicService,private http: HttpClient,private gptService: GptService) {}

  active_color="#ebaa96";
  inactive_color="#1890ff";

  selectedTab='Ongoing';
  tabs=[
    {
      name: 'Ongoing',
      iconType: 'hourglass',
      iconSpin: true, // initially false
      iconTheme: 'twotone',
      color: this.active_color,
    },
    {
      name: 'Upcoming',
      iconType: 'notification',
      iconSpin: false, // initially false
      iconTheme: 'twotone',
      color: this.inactive_color, // default color
    },
    {
      name: 'Ended',
      iconType: 'trophy',
      iconSpin: false, // initially false
      iconTheme: 'twotone',
      color: this.inactive_color, // default color
    }
  ];

  tabChange(index: number): void {
    this.tabs.forEach((tab,i) => {
      if(i===index) {
        tab.iconSpin=true;
        tab.color=this.active_color; // active color
      }
      else {
        tab.iconSpin=false;
        tab.color=this.inactive_color; // default color
      }
    });
    this.selectedTab=this.tabs[index].name;
  }


  sendMessage(message: string): void {
    //清空上一次的聊天记录
    this.gptResponse='';


    // Switch to query icon and progress bar mode when sending a message
    this.submitIconType=this.queryIconType;
    this.progressBarMode=this.queryProgressBarMode;

    this.subscription=this.gptService.traderChatWithGpt(message).subscribe(
      (event: MessageEvent) => {
        // Parse the response data
        const eventData=JSON.parse(event.data);
        const content=eventData.choices[0]?.delta?.content||'';

        // Append the content to gptResponse
        this.gptResponse+=content;

        // Switch back to default icon and progress bar mode when response is received
        this.submitIconType=this.defaultIconType;
        this.progressBarMode=this.defaultProgressBarMode;
      },
      err => {
        // Handle error
        this.gptResponse='An error occurred. Please try again.';

        // Switch back to default icon and progress bar mode when an error occurs
        this.submitIconType=this.defaultIconType;
        this.progressBarMode=this.defaultProgressBarMode;
      }
    );
  }


  ngOnDestroy() {
    // Clean up the subscription when the component is destroyed
    if(this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  ngOnInit(): void {
    // this.bwicService.getBwics();




  }

}
