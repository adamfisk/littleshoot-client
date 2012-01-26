#!/usr/bin/env bash

function die()
{
  echo $*
  svn revert settings.py
  exit 1
}

perl -pi -e "s/875676e62306905463045067eb30aaba/FACEBOOK_API_KEY_TOKEN/g" settings.py || die "Could not replace key"
perl -pi -e "s/201c791dda715fa1d85bafea5816f797/FACEBOOK_SECRET_KEY_TOKEN/g" settings.py || die "Could not replace key"

perl -pi -e "s/04G2SEBTMTS8S59X1SR2/awsAccessKeyIdToken/g" settings.py || die "Could not replace key ID"

# These don't quite work for some reason...
perl -pi -e "s,f3Z4AAPp6k+qxYwg9sDU/09Esyn7wnz3ZS5DsMsg,awsSecretAccessKeyToken,g" settings.py || die "Could not replace key"
perl -pi -e "s,{ProductToken}AAIGQXBwVGtufvthiqYFvk5JRCSzxan0Vcohy9lWx9BCMddDS8hSp0AYlkWPN4p1L8aQAsagWjyHR2/ZNBQ/lZQTK/EcqkTsmfsSvmRZXi4ssw0GheJ9ZT2a/Kauu4t/BODaAVBj1TtVvA8B+uaZ43US/enPoBJXmw8d32ObnesZy8n0sAIJHpA8xZg1xkcVg/f/11aSjh09qDxtKZwuQS+eivNAXWT9zok65EdTeqwOvMwZk/7LKRJA1N7FbHpcmeOWe7+7z349+T+q5wv6tQ4F3nyle+Qai0AkTZpg0yrG2gIvrQFM0FY=,awsProductTokenToken,g" settings.py || die "Could not replace product token"
