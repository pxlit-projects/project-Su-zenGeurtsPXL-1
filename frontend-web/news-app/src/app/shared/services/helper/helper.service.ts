import {Injectable} from "@angular/core";

@Injectable({
  providedIn: 'root'
})

export class HelperService {
  transformDate(date: string): string {
    const dateDate = new Date(date);
    return dateDate.toLocaleDateString('en-US', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric', hour: 'numeric', minute: 'numeric' });
  }

  transformDateShort(date: string): string {
    const dateDate = new Date(date);
    const day = String(dateDate.getDate()).padStart(2, '0');
    const month = String(dateDate.getMonth() + 1).padStart(2, '0'); // Months are zero-based
    const year = dateDate.getFullYear();
    const hours = String(dateDate.getHours()).padStart(2, '0');
    const minutes = String(dateDate.getMinutes()).padStart(2, '0');

    return `${day}/${month}/${year} ${hours}:${minutes}`;
  }

  toPascalCasing(word: string): string {
    return word.charAt(0).toUpperCase() + word.slice(1).toLowerCase();
  }

  // orderReviewsToMostRecent(reviews: Review[]): Review[] {
  //   return reviews.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
  // }
}
