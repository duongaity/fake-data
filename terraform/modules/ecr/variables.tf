variable "environment" {
  type        = string
  description = "Environment name"
}

variable "repository_name" {
  type        = string
  description = "Name of the ECR repository"
}

variable "image_tag_mutability" {
  type        = string
  description = "The tag mutability setting for the repository (MUTABLE or IMMUTABLE)"
  default     = "MUTABLE"
}

variable "scan_on_push" {
  type        = bool
  description = "Enable scanning of images on push"
  default     = true
}

variable "aws_account_id" {
  type        = string
  description = "AWS Account ID"
}