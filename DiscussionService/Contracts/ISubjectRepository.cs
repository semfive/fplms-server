using DiscussionService.Models;

namespace DiscussionService.Contracts
{
    public interface ISubjectRepository : IRepositoryBase<Subject>
    {
        Task<IEnumerable<Subject>> GetAllSubjectsAsync();
        Task<Subject> GetSubjectByIdAsync(Guid subjectId);
        void CreateSubject(Subject subject);
        void UpdateSubject(Subject subject);
        void DeleteSubject(Subject subject);
    }
}