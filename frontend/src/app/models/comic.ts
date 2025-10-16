export interface Comic {
  id: number;
  title: string;
  author: string;
  issueNumber?: number | null;
  releaseDate: string;
  coverImgUrl: string;
}
