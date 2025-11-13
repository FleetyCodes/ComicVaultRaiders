export interface Comic {
  id: number;
  title: string;
  author: string;
  illustrator: string;
  publisher: string;
  format: string;
  issueNumber?: number | null;
  releaseDate: string;
  coverImgUrl: string;
}
