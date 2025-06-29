include {
  path = find_in_parent_folders()
}

terraform {
  source = "../../../../modules/backend"
}

inputs = {
  bucket_name          = "fake-data-terraform-state-bucket"
  dynamodb_table_name  = "fake-data-terraform-locks"
  environment          = "nonprod"
}