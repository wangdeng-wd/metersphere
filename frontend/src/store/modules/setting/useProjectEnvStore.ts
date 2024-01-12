import { defineStore } from 'pinia';

export const ALL_PARAM = 'allParam';

const useProjectEnvStore = defineStore(
  'projectEnv',
  () => {
    const currentId = ref<string | number>(1);
    const httpNoWarning = ref(true);
    const getCurrentId = computed(() => currentId.value);
    const getHttpNoWarning = computed(() => httpNoWarning.value);
    function setCurrentId(id: string | number) {
      currentId.value = id;
    }
    function setHttpNoWarning(noWarning: boolean) {
      httpNoWarning.value = noWarning;
    }
    return { getCurrentId, currentId, httpNoWarning, setCurrentId, setHttpNoWarning, getHttpNoWarning };
  },
  {
    persist: {
      key: 'projectEnv',
      paths: ['httpNoWarning'],
    },
  }
);

export default useProjectEnvStore;