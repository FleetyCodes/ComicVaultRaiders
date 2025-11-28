export interface Comic {
  id?: number | null;
  title: string;
  author: string;
  illustrator: string;
  publisher: string;
  format: string;
  issueNumber?: number | null;
  releaseDate: string;
  coverImgUrl: string;
}
