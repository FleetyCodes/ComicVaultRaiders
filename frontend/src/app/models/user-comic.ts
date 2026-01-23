import { Comic } from './comic';

export interface UserComic{
  id?: number | null;
  comic: Comic;
  positiveDescription?: string | null;
  negativeDescription?: string | null;
  artRate: number;
  storyRate: number;
  panelRate: number;
  wishlisted: boolean;
}
