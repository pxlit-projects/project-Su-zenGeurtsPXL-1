import {Review} from "./review.model";
import {Comment} from "./comment.model";

export interface Post {
  id?: number;
  title: string;
  content: string;
  userId: number;
  category: string;
  createdAt: string;
  state: string;
  reviews?: Review[];
  comments?: Comment[];
}
