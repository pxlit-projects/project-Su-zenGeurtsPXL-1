export interface Review {
  id?: number;
  userId: number;
  postId: number;
  content: string;
  createdAt: string;
  type: string;
}
