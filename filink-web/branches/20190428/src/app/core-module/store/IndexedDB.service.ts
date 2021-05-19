import {Injectable} from '@angular/core';

@Injectable()
export class IndexedDBService {
  private database;
  private version;
  private db;
  private store;
  constructor () {
  }

  init (database, version, store = null) {
    this.database = database;
    this.version = version;
    this.db = null;
    this.store = store;
  }

  callback () {
    console.log(`${this.database}, ${this.version}`);
  }

  open (table, pushErr = (err) => {throw new Error(err); }) {
    let request;
    if (window.indexedDB) {
      request = window.indexedDB.open(this.database, this.version);
      request.onerrror = (event) => {
        pushErr(event);
      };
      request.onsuccess = (event) => {
        this.db = request.result;
        console.log('get db');
        let objectStore;
        if (this.db && !this.db.objectStoreNames.contains(table.name)) {
          objectStore = this.db.createObjectStore(table.name, table.options);
          // 对name字段建立索引
          table.indexs.map( (index) => {
            objectStore.createIndex(index.name, index.prop, index.option);
          });
          console.log('success');
        }
      };
      request.onupgradeneeded = (event) => {
        this.db = event.target.result;
        console.log('upgrade');
        let objectStore;
        if (this.db && !this.db.objectStoreNames.contains(table.name)) {
          objectStore = this.db.createObjectStore(table.name, table.options);
          // 对name字段建立索引
          table.indexs.map( (index) => {
            objectStore.createIndex(index.name, index.prop, index.option);
          });
          console.log('success');
        }
      };
    }
  }


  addAll (store, table, pushErr = (err) => {throw new Error(err); }) {

    if (this.db) {

      const transaction = this.db.transaction([table], 'readwrite');
      const objectStore = transaction.objectStore(table);
      store.map((item) => {
        const request = objectStore.put(item);
        request.onsuccess = (event) => {
          console.log('add success');
        };
        request.onerror = (event) => {
          pushErr(event.target.error);
        };
      });
    }

  }

  add (item, table, pushErr = (err) => {throw err; }) {
    if (this.db) {

      const transaction = this.db.transaction([table], 'readwrite');
      const objectStore = transaction.objectStore(table);
      const request = objectStore.put(item);

      request.onsuccess = (event) => {
        console.log('add success');
      };

      request.onerror = (event) => {
        pushErr(event.target.error);
      };
    }

  }

  read (i, table, pushErr = (err) => {throw new Error(err); }) {
    if (this.db) {
      let getData;
      const transaction = this.db.transaction([table]);
      const objectStore = transaction.objectStore(table);
      const request = objectStore.get(i);
      request.onerror = (event) => {
        pushErr('读取异常');
      };
      request.onsuccess = (event) => {
        if (request.result) {
          getData = request.result;
        } else {
          pushErr('没有数据');
        }
      };
      return getData;
    }
  }



  readAll (table, pushErr = (err) => {throw new Error(err); }) {
    const getData = [];
    const objectStore = this.db.transaction(table).objectStore(table);

    objectStore.openCursor().onsuccess =  (event) => {
      const cursor = event.target.result;
      if (cursor) {
        getData.push(cursor.value);
        // console.log(cursor.value.name, cursor.value.id, cursor.value.email);
        // console.log(cursor.key, cursor.value.name, cursor.value.age, cursor.value.email);
        cursor.continue();
      } else {
        console.log('没有更多数据了');
      }
    };

    return getData;

  }

  // 删除 指定行
  remove (i, table, pushErr = (err) => {throw new Error(err); }) {
    const request = this.db.transaction([table], 'readwrite').objectStore(table).delete(i);
    request.onsuccess = (event) => {
      console.log('remove success');
    };
    request.onerror = (event) => {
      pushErr('删除失败');
    };
  }

  removeAll (pushErr = (err) => {throw new Error(err); }) {
    const request = this.db.transaction(['person'], 'readwrite').objectStore('person').clear();
    request.onsuccess = (event) => {
      console.log('clear success');
    };
    request.onerror = (event) => {
      pushErr(event.target.error);
    };
  }

  // 指定更新
  update (o, table, pushErr = (err) => {throw new Error(err); }) {
    const request = this.db.transaction([table], 'readwrite').objectStore(table)
      .put(o);

    request.onsuccess = (event) => {
      console.log('update success');
    };
    request.onerror = (event) => {
      pushErr(event);
    };
  }


}
