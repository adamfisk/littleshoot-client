#!/usr/bin/env bash

function die()
{
  echo $*
  svn revert settings.py
  exit 1
}

perl -pi -e "s/FACEBOOK_API_KEY_TOKEN/875676e62306905463045067eb30aaba/g" settings.py || die "Could not replace key"
perl -pi -e "s/FACEBOOK_SECRET_KEY_TOKEN/201c791dda715fa1d85bafea5816f797/g" settings.py || die "Could not replace key"

./release.bash 04G2SEBTMTS8S59X1SR2 f3Z4AAPp6k+qxYwg9sDU/09Esyn7wnz3ZS5DsMsg {ProductToken}AAIGQXBwVGtufvthiqYFvk5JRCSzxan0Vcohy9lWx9BCMddDS8hSp0AYlkWPN4p1L8aQAsagWjyHR2/ZNBQ/lZQTK/EcqkTsmfsSvmRZXi4ssw0GheJ9ZT2a/Kauu4t/BODaAVBj1TtVvA8B+uaZ43US/enPoBJXmw8d32ObnesZy8n0sAIJHpA8xZg1xkcVg/f/11aSjh09qDxtKZwuQS+eivNAXWT9zok65EdTeqwOvMwZk/7LKRJA1N7FbHpcmeOWe7+7z349+T+q5wv6tQ4F3nyle+Qai0AkTZpg0yrG2gIvrQFM0FY= || die "Error releasing!!"

svn revert settings.py
# aws_access_key_id = '04G2SEBTMTS8S59X1SR2'
# aws_secret_access_key = 'sb8jgLA+PAOvX6+BJfCDyMjZmuA1miP+srSzO9J4'
# product_token = '{ProductToken}AAIGQXBwVGtufvthiqYFvk5JRCSzxan0Vcohy9lWx9BCMddDS8hSp0AYlkWPN4p1L8aQAsagWjyHR2/ZNBQ/lZQTK/EcqkTsmfsSvmRZXi4ssw0GheJ9ZT2a/Kauu4t/BODaAVBj1TtVvA8B+uaZ43US/enPoBJXmw8d32ObnesZy8n0sAIJHpA8xZg1xkcVg/f/11aSjh09qDxtKZwuQS+eivNAXWT9zok65EdTeqwOvMwZk/7LKRJA1N7FbHpcmeOWe7+7z349+T+q5wv6tQ4F3nyle+Qai0AkTZpg0yrG2gIvrQFM0FY='
