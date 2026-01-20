package com.b2b.b2b.shared.multitenancy;

public class OrganizationContext {
    private static final ThreadLocal<Integer> currentOrgId = new ThreadLocal<>();

    public static void setOrgId(Integer orgId)
    {
        currentOrgId.set(orgId);
    }
    public static Integer getOrgId()
    {
        return currentOrgId.get();

    }
    public static void clear()
    {
        currentOrgId.remove();
    }
}
