from rest_framework import permissions
class IsAllowedDelete(permissions.BasePermission):        
    def has_permission(self, request, view):
        if request.method == "DELETE":
             print('id : ',request.DELETE["id"])
             return True
        else: 
            return False 