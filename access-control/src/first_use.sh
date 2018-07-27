#!/bin/bash

v1: --vertex=organization add --property=name Kofre --property=code 1
v2: --vertex=unitOrganization add --property=name Bahia --property=code 1
e1: --edge=has add --source=v1 --target=v2 // (v1 -> v2)
v3: --vertex=group add --property=name Marketing --property=code 1
e2: --edge=has add --source=v2 --target=v3 // (v2 -> v3)
v4: --vertex=accessRule add
e3: --edge=provide --source=v4 --target=v3 // (v4 -> v3)
v5: --vertex=user add --property=name User1 --property=code 1
e4: --edge=associated add --source=v5 --target=v4 // (v5 -> v4)
v6: --vertex=accessGroup add --property=name Administration --property=code 1
e5: --edge=own add --source=v4 --target=v6  // (v4 -> v6)
v7: --vertex=rule add --property=name remove_user --property=code 1
v8: --vertex=rule add --property=name add_user --property=code 2
v9: --vertex=rule add --property=name edit_user --property=code 3
e6: --edge=add add --source=v6 --target=v7 // (v6 -> v7)
e7: --edge=add add --source=v6 --target=v8  // (v6 -> v8)
e8: --edge=add add --source=v6 --target=v9  // (v6 -> v9)

v10: --vertex=accessGroup add --property=name Operator --property=code 2
e9: --edge=inherit add --source=v6 --target=v10  // (v6 -> v10)
v11: --vertex=rule add --property=name view_user --property=code 3
e10: --edge=add add --source=v10 --target=v11  // (v10 -> v11)
v12: --vertex=user add --property=name User2 --property=code 2
v13: --vertex=accessRule add
e11: --edge=associated add --source=v12 --target=v13  // (v12 -> v13)
e12: --edge=provide add --source=v13 --target=v1  // (v13 -> v1)
e13: --edge=own add --source=v13 --target=v6  // (v13 -> v6)

