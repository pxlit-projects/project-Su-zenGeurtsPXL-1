export interface Notification {
  id?: number;
  postId: number;
  receiverId: number;
  executorId: number;
  content: string;
  action: string;
  executedAt: string;
  isRead: boolean;
}
