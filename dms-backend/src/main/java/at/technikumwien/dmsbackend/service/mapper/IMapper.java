package at.technikumwien.dmsbackend.service.mapper;

public interface IMapper {
    public interface Mapper<S, T> {
        T mapToDto(S source);
        S mapToEntity(T target);
    }
}