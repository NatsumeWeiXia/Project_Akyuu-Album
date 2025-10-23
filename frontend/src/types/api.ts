export interface User {
  id: number
  username: string
  nickname: string
  email?: string
  bio?: string
  avatar?: string
  avatarUrl?: string
  role: string
  createdAt: string
  updatedAt: string
}

export interface Album {
  id: number
  name: string
  description?: string
  isPublic: boolean
  ownerId: number
  coverImage?: string
  createdAt: string
  updatedAt: string
  owner?: User
  members?: readonly AlbumMember[]
  memberCount?: number
  mediaCount?: number
}

export interface AlbumMember {
  id: number
  albumId: number
  userId: number
  role: string
  canUpload: boolean
  createdAt: string
  user?: User
}

export interface Media {
  id: number
  albumId: number
  uploaderId: number
  title?: string
  filename: string
  contentType: string
  sizeBytes: number
  width?: number
  height?: number
  durationSeconds?: number
  storagePath: string
  thumbnailPath?: string
  createdAt: string
  updatedAt: string
  uploader?: User
  album?: Album
  type: 'image' | 'video' | 'other'
  url: string
  thumbnailUrl?: string
}

export interface Comment {
  id: number
  mediaId: number
  authorId: number
  parentId?: number
  content: string
  createdAt: string
  updatedAt: string
  author?: User
  replies?: readonly Comment[]
}

export interface PageResponse<T> {
  content: readonly T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
}

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  password: string
  nickname: string
  avatarUrl?: string
}

export interface AlbumRequest {
  name: string
  description?: string
  isPublic: boolean
}

export interface CommentRequest {
  content: string
  parentId?: number
}

export interface ApiResponse<T = unknown> {
  code: number
  message: string
  data?: T
  details?: unknown
}