import {Component, Input, OnInit} from '@angular/core';
import {NgForOf, NgIf} from "@angular/common";

@Component({
  selector: 'app-star-rating',
  standalone: true,
  imports: [
    NgForOf,
    NgIf
  ],
  templateUrl: './star-rating.component.html',
  styleUrl: './star-rating.component.scss'
})
export class StarRatingComponent implements OnInit {
  @Input({required: true}) rating!: number;
  @Input({required: true}) max!: number;

  fullStars: number = 0;
  blankStars: number = 0;
  halfStar: boolean = false;

  ngOnInit() {
    const roundedRating = this.roundHalf(this.rating);
    this.fullStars = Math.floor(roundedRating);
    this.halfStar = roundedRating > this.fullStars && roundedRating < this.fullStars + 1;
    this.blankStars = this.max - this.fullStars - (this.halfStar ? 1 : 0);
  }

  roundHalf(num: number): number {
    const decimalPart = num % 1;
    if (decimalPart < 0.25) {
      return Math.floor(num); // round down
    } else if (decimalPart < 0.75) {
      return Math.floor(num) + 0.5; // round to the nearest half
    } else {
      return Math.ceil(num); // round up
    }
  }
}
