from django.core.serializers.json import DjangoJSONEncoder
from django.db.models.query import QuerySet

def maybe_call(x):
    if callable(x): return x()
    return x


class JSONEncoder(DjangoJSONEncoder):
    '''An extended JSON encoder to handle some additional cases.

    The Django encoder already deals with date/datetime objects.
    Additionally, this encoder uses an 'as_dict' or 'as_list' attribute or
    method of an object, if provided. It also makes lists from QuerySets.
    '''
    def default(self, obj):
        if hasattr(obj, 'as_dict'):
            return maybe_call(obj.as_dict)
        elif hasattr(obj, 'as_list'):
            return maybe_call(obj.as_list)
        elif isinstance(obj, QuerySet):
            return list(obj)
        return super(JSONEncoder, self).default(obj)
json_encode = JSONEncoder().encode